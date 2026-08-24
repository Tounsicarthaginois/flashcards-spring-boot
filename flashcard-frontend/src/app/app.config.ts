// Configuration globale : c'est ici qu'on branche les outils dont
// toute l'application se sert. Notamment l'interceptor JWT, déclaré
// une seule fois ici et appliqué automatiquement à chaque requête.

import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { providePrimeNG } from 'primeng/config';
import Aura from '@primeng/themes/aura';
import { routes } from './app.routes';
import { authInterceptor } from './interceptors/auth.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    // Active la navigation entre les pages
    provideRouter(routes),

    // Active les appels réseau, et fait passer chacun d'eux par
    // authInterceptor. C'est grâce à cette ligne qu'aucun service
    // n'a besoin de s'occuper du token lui-même.
    provideHttpClient(withInterceptors([authInterceptor])),
    providePrimeNG({
      theme: {
        preset: Aura,
        options: {
          darkModeSelector: false
        }
      },
      ripple: true
    })
  ]
};
