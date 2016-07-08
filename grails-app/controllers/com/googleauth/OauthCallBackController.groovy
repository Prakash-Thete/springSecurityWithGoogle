package com.googleauth

import com.auth.Role
import com.auth.User
import com.auth.UserRole
import com.constants.CodeConstants
import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import org.scribe.model.Token
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails

@Secured(["IS_AUTHENTICATED_ANONYMOUSLY"])
class OauthCallBackController {

    def grailsApplication
    def oauthService
    def springSecurityService
    def userDetailsService

    /**
     * Action for handling the success handler of google oauth
     *
     * @return
     */
    def google() {
        try {

            //get google token
            Token googleAccessToken = (Token) session[oauthService.findSessionKeyForAccessToken('google')]

            println "googleAccessToken : " + googleAccessToken

            if (googleAccessToken) {

                //get users data
                def googleResource = oauthService.getGoogleResource(googleAccessToken, grailsApplication.config.grails.google.api.url)
                def googleResponse = JSON.parse(googleResource?.getBody())

                Map data = [id    : googleResponse.id, email: googleResponse.email, name: googleResponse.name, given_name: googleResponse.given_name, family_name: googleResponse.family_name,
                            gender: googleResponse.gender, link: googleResponse.link]

                println "data : " + data

                def username    = googleResponse.email
                def password
                UserDetails userDetails

                //check if user already exist or not
                def user        = User.findByUsername(username)
                if (user) {

                    //If user exists redirect him to index action
                    userDetails = userDetailsService.loadUserByUsername(username)

                    //setting spring security context
                    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                            userDetails, password == null ? userDetails.getPassword() : password, userDetails.getAuthorities()))

                    println "$username is logged in "

                    redirect(controller: 'provisioning', action: 'index')
                } else {

                    println "user with ${username} is not allowed to access this system"

                    /**
                     * If user does not exist, create new user entry for this user
                     * also assign the default role to him
                     */
                    user = new User( username   : username,
                                     password        : CodeConstants.SUPER_ADMIN_COMPANY_NAME,
                                     enabled    : true)
                    user.save(flush: true, failOnError: true)

                    println "New User saved"

                    //Assign "ROLE_SUPER_ADMIN" role to newly created user
                    def userRole = Role.findByAuthority(CodeConstants.ROLE_SUPER_ADMIN)
                    UserRole newUserRole = UserRole.get(user.id, userRole.id)
                    if (!newUserRole) {
                        UserRole.create(user, userRole, true)
                    }

                    //setting spring security context
                    userDetails = userDetailsService.loadUserByUsername(username)
                    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                            userDetails, password == null ? userDetails.getPassword() : password, userDetails.getAuthorities()))

                    println "$username is logged in "

                    redirect(controller: 'provisioning', action: 'index')
                }
            } else {
                println "Token not found."
            }
        } catch (Exception e){
            println "Exception occurred while oauth login -> google" + e
        }
    }

    /**
     * For handling the failure requests
     * @return
     */
    def failure() {
        println "Error occurred"
    }
}
