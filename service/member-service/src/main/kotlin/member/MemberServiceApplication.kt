package member

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(
    scanBasePackages = ["member", "security", "db"]
)
class MemberServiceApplication

fun main(args: Array<String>) {
    runApplication<MemberServiceApplication>(*args)
}