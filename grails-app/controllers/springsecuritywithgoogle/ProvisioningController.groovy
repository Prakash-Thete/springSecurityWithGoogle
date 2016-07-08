package springsecuritywithgoogle

import grails.plugin.springsecurity.annotation.Secured

class ProvisioningController {

    @Secured(['ROLE_SUPER_ADMIN'])
    def index() {

    }
}
