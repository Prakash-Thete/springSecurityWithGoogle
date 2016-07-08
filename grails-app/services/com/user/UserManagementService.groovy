package com.user

import com.auth.Role
import com.auth.User
import com.auth.UserRole
import com.constants.CodeConstants
import grails.transaction.Transactional

@Transactional
class UserManagementService {

    void bootstrapSystemRoles(){

        /* Create the SUPER_ADMIN user role. */
        if(!Role.findByAuthority(CodeConstants.ROLE_SUPER_ADMIN)) {
            def superAdminRole = new Role(authority: CodeConstants.ROLE_SUPER_ADMIN).save(flush: true)
        }

        /* Create a super admin user. */
        User superAdmin = User.findByUsername(CodeConstants.SUPER_ADMIN_USER_NAME)
        if(!superAdmin) {
            superAdmin = new User(
                    username        : CodeConstants.SUPER_ADMIN_USER_NAME,
                    password        : CodeConstants.SUPER_ADMIN_COMPANY_NAME,
                    enabled         : true)

            superAdmin.save(flush: true, failOnError: true)

            def superAdminRole = Role.findByAuthority(CodeConstants.ROLE_SUPER_ADMIN)
            UserRole superAdminUserRole = UserRole.get(superAdmin.id, superAdminRole.id)
            if(!superAdminUserRole) {
                UserRole.create(superAdmin, superAdminRole, true)
            }
        }

    }


    User findUserByUsername(String userName) {
        try {
            return User.findByUsername(userName)
        } catch (Exception e) {
            println "Exception while finding user by its userName -> findUserByUsername"
        }
    }
}
