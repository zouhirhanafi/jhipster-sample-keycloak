import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'parameter',
        data: { pageTitle: 'jhipsterSampleKeycloakApp.jhipsterSampleKeycloakParameter.home.title' },
        loadChildren: () =>
          import('./jhipsterSampleKeycloak/parameter/parameter.module').then(m => m.JhipsterSampleKeycloakParameterModule),
      },
      {
        path: 'conteneur',
        data: { pageTitle: 'jhipsterSampleKeycloakApp.jhipsterSampleKeycloakConteneur.home.title' },
        loadChildren: () =>
          import('./jhipsterSampleKeycloak/conteneur/conteneur.module').then(m => m.JhipsterSampleKeycloakConteneurModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
