import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IParameter } from '../parameter.model';
import { ParameterService } from '../service/parameter.service';

@Component({
  templateUrl: './parameter-delete-dialog.component.html',
})
export class ParameterDeleteDialogComponent {
  parameter?: IParameter;

  constructor(protected parameterService: ParameterService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.parameterService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
