rootProject.name = "cvs-server"

include("contract")
include("contract:contract-api")
include("contract:contract-event")

include("gateway")

include("service")
include("service:product-service")
include("service:member-service")
include("service:auth-service")
include("service:crawler-service")

include("infra")
include("infra:infra-db")