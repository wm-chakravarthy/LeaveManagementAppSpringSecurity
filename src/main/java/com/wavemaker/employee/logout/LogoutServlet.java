package com.wavemaker.employee.logout;

import com.wavemaker.employee.exception.ServerUnavailableException;
import com.wavemaker.employee.service.UserCookieService;
import com.wavemaker.employee.service.impl.UserCookieServiceImpl;
import com.wavemaker.employee.util.CookieHandler;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logout")
public class LogoutServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(LogoutServlet.class);

    private UserCookieService userCookieService;

    @Override
    public void init(ServletConfig config) {
        userCookieService = new UserCookieServiceImpl();
    }

    @PostMapping
    public void logout(HttpServletRequest request, HttpServletResponse response) throws ServerUnavailableException {
        HttpSession session = request.getSession(false);
        String cookieName = "my_auth_cookie";
        String cookieValue = null;

        if (session != null) {
            session.invalidate();
            logger.info("User session invalidated successfully");
        }
        cookieValue = CookieHandler.getCookieValueByCookieName(cookieName, request);
        if (cookieValue != null) {
            userCookieService.deleteUserCookie(cookieValue);
            logger.info("User cookie deleted successfully");
        }
        // Invalidate the authentication cookie
        Cookie cookie = CookieHandler.invalidateCookie(cookieName, request);
        response.addCookie(cookie);

    }
}
