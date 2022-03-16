import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ConteneurComponent } from './list/conteneur.component';
import { ConteneurDetailComponent } from './detail/conteneur-detail.component';
import { ConteneurUpdateComponent } from './update/conteneur-update.component';
import { ConteneurDeleteDialogComponent } from './delete/conteneur-delete-dialog.component';
import { ConteneurRoutingModule } from './route/conteneur-routing.module';

@NgModule({
  imports: [SharedModule, ConteneurRoutingModule],
  declarations: [ConteneurComponent, ConteneurDetailComponent, ConteneurUpdateComponent, ConteneurDeleteDialogComponent],
  entryComponents: [ConteneurDeleteDialogComponent],
})
export class JhipsterSampleKeycloakConteneurModule {}
