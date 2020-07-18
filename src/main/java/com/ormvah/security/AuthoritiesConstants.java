package com.ormvah.security;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String USER = "ROLE_USER";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";

    public static final String AGENT_BO = "ROLE_AGENT_BO";
    public static final String DIRECTEUR = "ROLE_DIRECTEUR";
    public static final String CHEF_DE_SERVICE = "ROLE_CHEF_DE_SERVICE";
    public static final String AGENT_CAI = "ROLE_AGENT_CAI";
    public static final String CHARGE_DE_REQUETE = "ROLE_CHARGE_DE_REQUETE";
    public static final String CHARGE_DE_TRAITEMENT_COURRIER = "ROLE_CHARGE_DE_TRAITEMENT_COURRIER";

    private AuthoritiesConstants() {
    }
}
