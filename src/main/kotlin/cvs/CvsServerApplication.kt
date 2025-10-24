package cvs

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CvsServerApplication

fun main(args: Array<String>) {
	runApplication<CvsServerApplication>(*args)
}
