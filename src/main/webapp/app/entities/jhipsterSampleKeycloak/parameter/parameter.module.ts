import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ParameterComponent } from './list/parameter.component';
import { ParameterDetailComponent } from './detail/parameter-detail.component';
import { ParameterUpdateComponent } from './update/parameter-update.component';
import { ParameterDeleteDialogComponent } from './delete/parameter-delete-dialog.component';
import { ParameterRoutingModule } from './route/parameter-routing.module';

@NgModule({
  imports: [SharedModule, ParameterRoutingModule],
  declarations: [ParameterComponent, ParameterDetailComponent, ParameterUpdateComponent, ParameterDeleteDialogComponent],
  entryComponents: [ParameterDeleteDialogComponent],
})
export class JhipsterSampleKeycloakParameterModule {}
