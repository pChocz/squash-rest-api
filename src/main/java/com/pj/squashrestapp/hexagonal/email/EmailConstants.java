package com.pj.squashrestapp.hexagonal.email;

import lombok.experimental.UtilityClass;

@UtilityClass
class EmailConstants {

    static final String EMAIL_TEMPLATE = "email_template.html";
    static final String EMAIL_TEMPLATE_EXCEPTION = "email_template_exception.html";
    static final String EMAIL_TEMPLATE_USER_INFO = "email_template_user_info.html";

    static final String ADMIN_EMAIL_HREF =
            "<a href =\"mailto: admin@squash-app.win\" style=\"color: #0000EE;\">admin@squash-app.win</a>";

    static final String SQUASH_APP_HREF = "<a href=\"https://squash-app.win\" style=\"color: #0000EE;\">Squashable</a>";

    static final String MY_WEBSITE_HREF =
            "<a href=\"https://www.choczynski.pl\" style=\"color: #0000EE;\">Piotr Choczyński</a>";
}
