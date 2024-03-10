package com.example.mobilemap;

import android.app.Activity;
import android.content.Intent;

/**
 * Gestionnaire de navigation
 *
 * @author J.Houdé
 */
public class NavigationHandler {
    private final int navigationButtonId;

    private final Intent destinationIntent;
    private final Activity activity;
    private final int currentPageId;

    public void setNextNavigationHandler(NavigationHandler nextNavigationHandler) {
        this.nextNavigationHandler = nextNavigationHandler;
    }

    private NavigationHandler nextNavigationHandler;

    /**
     * Gestionnaire de navigation
     *
     * @param navigationButtonId identifiant du bouton de navigation à gérer
     * @param destinationIntent intent pour le changement d'activité
     * @param activity activité mère
     * @param currentPageId identifiant de la page actuelle
     */
    public NavigationHandler(int navigationButtonId, Intent destinationIntent, Activity activity, int currentPageId) {
        this.navigationButtonId = navigationButtonId;
        this.destinationIntent = destinationIntent;
        this.activity = activity;
        this.currentPageId = currentPageId;
    }

    /**
     * Gestion de la requête de changement d'activité
     * @param menuId identifiant du bouton appuyé
     * @return vrai pour faire apparaitre le bouton comme sélectionné, sinon faux
     */
    public boolean handle(int menuId) {
        if (menuId != navigationButtonId) { // passer au prochain élément de la châine s'il existe
            if (nextNavigationHandler == null) {
                return false;
            }

            return nextNavigationHandler.handle(menuId);
        }

        if(currentPageId == menuId) { // la page demandée est déjà affichée
            return true;
        }

        activity.startActivity(destinationIntent); // lancer l'activité voulue

        return true;
    }
}
