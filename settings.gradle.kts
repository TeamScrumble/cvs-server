rootProject.name = "cvs-server"

include("contract")
include("contract:contract-api")
include("contract:contract-api:client")
include("contract:contract-event")

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
include("infra:infra-security")
