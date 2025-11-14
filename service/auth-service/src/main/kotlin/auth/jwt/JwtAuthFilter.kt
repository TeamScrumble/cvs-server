//package auth.jwt
//
//import jakarta.servlet.FilterChain
//import jakarta.servlet.http.HttpServletRequest
//import jakarta.servlet.http.HttpServletResponse
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
//import org.springframework.security.core.authority.SimpleGrantedAuthority
//import org.springframework.security.core.context.SecurityContextHolder
//import org.springframework.stereotype.Component
//import org.springframework.web.filter.OncePerRequestFilter
//
//@Component
//class JwtAuthFilter(
//    private val jwtService: JwtService
//) : OncePerRequestFilter() {
//
//    override fun doFilterInternal(req: HttpServletRequest, res: HttpServletResponse, chain: FilterChain) {
//        val header = req.getHeader("Authorization")
//        if (header?.startsWith("Bearer ") == true) {
//            val token = header.removePrefix("Bearer ").trim()
//
//            runCatching {
//                val claims = jwtService.parse(token)
//                val subject = claims.subject
//
//                if (subject != null && SecurityContextHolder.getContext().authentication == null) {
//                    val auth = UsernamePasswordAuthenticationToken(
//                        subject,
//                        null,
//                        listOf(SimpleGrantedAuthority("ROLE_USER"))
//                    )
//
//                    SecurityContextHolder.getContext().authentication = auth
//                }
//            }
//        }
//
//        chain.doFilter(req, res)
//    }
//
//}