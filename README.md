# springSecurityWithGoogle

Spring Security with Google Oauth
===================
----------
**Version specification :** Grail's : 2.4.4, Core Plugin : 2.0.0 and Oauth 2.5

Spring Security Core
----------------------------

**Reference link :**

http://grails-plugins.github.io/grails-spring-security-core/v2/index.html
http://grails-plugins.github.io/grails-spring-security-core/v2/guide/single.html#tutorials

**Step 1 : Configuration** 

**prerequisites** :

Create a new grails application : 
```
grails create-app springSecurityWithGoogle 
```

create a new database in mysql as : 
```
create database springSecurityWithGoogle;
```

**Modifying grails application configuration :**

dataSource configuration for mysql Configuring Datasource : DataSource.groovy
```
dataSource {
	   dbCreate = "create"
	   url = "jdbc:mysql://localhost/springSecurityWithGoogle"
	   driverClassName = "com.mysql.jdbc.Driver"
	   username = "root"
	   password = ""
}    
```

Add core plugin i.e. "spring-security-core:2.0.0" * to BuildConfig.groovy
```
plugins {
------------other plugins------------------------
   compile ":spring-security-core:2.0.0"
------------other plugins------------------------
}
```

**Step 2 : Compiling grail's application**

 Now compile the grail application using 
> grails compile 

so that, it will download the plugin dependencies.

**Step 3 : Domain class creation for spring security**

After the successful addition of plugin run below command from application directory path(command line)

> grails s2-quickstart com.auth User Role

here  com.auth is a package name for User and Role Domain classes

It will create three Domain classes for our application naming :

>- Role
>- User
>- UserRole

It will also add below things at the end of **config.groovy** file :
```
 // Added by the Spring Security Core plugin:
grails.plugin.springsecurity.userLookup.userDomainClassName = 'com.auth.User'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'com.auth.UserRole'
grails.plugin.springsecurity.authority.className = 'com.auth.Role'
grails.plugin.springsecurity.controllerAnnotations.staticRules = [
  '/':                ['permitAll'],
  '/index':           ['permitAll'],
  '/index.gsp':       ['permitAll'],
  '/assets/**':       ['permitAll'],
  '/**/js/**':        ['permitAll'],
  '/**/css/**':       ['permitAll'],
  '/**/images/**':    ['permitAll'],
  '/**/favicon.ico':  ['permitAll']
]
```

**Step 4 : Add user and role and association between them** 

 Before testing our applications security, we need to add user, role and role to the user.
 
 Here we are going to add this entries at the time of application Bootstrapping by creating the UserManagementService.
```
class BootStrap {

    def userManagementService

    def init = { servletContext ->

        //Bootstrapping System Roles
        userManagementService.bootstrapSystemRoles()
    }
    def destroy = {
    }
}

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
```    

**Step 5 : Adding Secured Annotation **

How to add annotations to controller action : 
Suppose we have a controller “**Provisioning**”, and it has a action “**index**” then adding annotations to it, will looks like:
```
@Secured(['ROLE_SUPER_ADMIN'])
def index() {

}
```

**Step 6 : Now run the application **

Try accessing above action to access "**index**" action you have to be user of the system and you should have a role “**ROLE_SUPER_ADMIN**”.

**What are the things this plugin will take care of :**
>- User Authentication
>- User Authorization
>- Preventing duplicate user registration


Google Oauth
----------------------------
**Reference link :**

https://grails.org/plugin/oauth
https://github.com/manishkbharti/grailsOauthPluginDemo


**Step 1 : Configuration**

Add project to google developer console

**Steps for adding project  to google developer console :** 

Step 1 : Goto https://console.developers.google.com/apis/library 
create project with name "**springSecurityWithGoogle**"
   
Step 2 : generate the client-id and secret for the project

Step 3 : Goto Credentials and click on the project just created
then put "**Authorized JavaScript origins"** as 
```
http://localhost:8080
```
If running on local host else the domain name.

put "**Authorized redirect URIs**" as
```
http://localhost:8080/springSecurityWithGoogle/oauth/google/callback
```
Developer console is ready.

**Project Configuration :** 

Add oauth plugin i.e. ":oauth:2.5" to BuildConfig.groovy
```
plugins {
------------other plugins------------------------
    compile ':oauth:2.5'
------------other plugins------------------------
}
```

Add below lines to config.groovy

```
//For google authentication
oauth {
    providers {
        google {
		    api = Google2Api
		    key = 'generated_key'
		    secret = 'generated_secret'
		
		    scope = 'https://www.googleapis.com/auth/userinfo.email'
			callback = "http://localhost:8080/springSecurityWithGoogle/oauth/google/callback"
            successUri = "http://localhost:8080/springSecurityWithGoogle/oauthCallBack/google"
        }
    }
}

grails.google.api.url="https://www.googleapis.com/oauth2/v1/userinfo"
```

Also add ```'/oauth/**' : ['permitAll'] ``` 
to ```grails.plugin.springsecurity.controllerAnnotations.staticRules = [```

**Step 2 : OauthCallBack controller creation ** 

Create an controller "**OauthCallBack**" for handling the google oauth redirection.
```
grails create-controller com.googleauth.OauthCallBack
```

Add action "google" foe handling the successfull authentication
```
def google() {
	//get google token
	Token googleAccessToken = (Token) session[oauthService.findSessionKeyForAccessToken('google')]

println "googleAccessToken : " + googleAccessToken

//Rest of the code 
}
```

Add ```<oauth:connect provider="google">Google</oauth:connect>``` to "auth.gsp" i.e. spring security login page in this case Or we can add it to our custom login page.

**Step 3: Run the application** 

```
grails clean && grails run-app
```
You will be prompted with login screen with "**Google**" link on it.

Click on the link and it will take you to the gmail accounts, grant access to the application.

And now you are now authenticated with google.

--------------------------------------------------------- Done -----------------------------------------------------------




 

