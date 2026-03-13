import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { IParameter } from '../parameter.model';

@Component({
  standalone: true,
  selector: 'jhi-parameter-detail',
  templateUrl: './parameter-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class ParameterDetailComponent {
  parameter = input<IParameter | null>(null);

  previousState(): void {
    window.history.back();
  }
}
