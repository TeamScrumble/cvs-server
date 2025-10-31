rootProject.name = "cvs-server"

include("core")
include("core:core-api")

include("clients")
include("clients:client-common")
include("clients:client-crawler")

include("storage")
include("storage:db-core")
include("storage:storage-kafka")
include("storage:storage-kafka:kafka-event")

include("crawler")