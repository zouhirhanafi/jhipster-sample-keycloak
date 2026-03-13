import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { IConteneur } from '../conteneur.model';

@Component({
  standalone: true,
  selector: 'jhi-conteneur-detail',
  templateUrl: './conteneur-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class ConteneurDetailComponent {
  conteneur = input<IConteneur | null>(null);

  previousState(): void {
    window.history.back();
  }
}
