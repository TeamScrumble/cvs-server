rootProject.name = "cvs-server"

include("contract")
include("contract:contract-api")
include("contract:contract-api:client")
include("contract:contract-event")
include("contract:contract-error")

include("edge")
include("edge:gateway")
include("edge:discovery")

include("service")
include("service:product-service")
include("service:member-service")
include("service:auth-service")
include("service:crawler-service")

include("infra")
include("infra:infra-db")
include("infra:infra-cache")
include("infra:infra-message-broker")
include("infra:infra-security")
include("infra:infra-mail")

include("support")
include("support:support-docs")