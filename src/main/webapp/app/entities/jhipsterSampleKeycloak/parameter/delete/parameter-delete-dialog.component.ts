import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IParameter } from '../parameter.model';
import { ParameterService } from '../service/parameter.service';

@Component({
  standalone: true,
  templateUrl: './parameter-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class ParameterDeleteDialogComponent {
  parameter?: IParameter;

  protected parameterService = inject(ParameterService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.parameterService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
