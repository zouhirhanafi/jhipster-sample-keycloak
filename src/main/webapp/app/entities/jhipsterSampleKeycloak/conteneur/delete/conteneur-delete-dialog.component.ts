import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IConteneur } from '../conteneur.model';
import { ConteneurService } from '../service/conteneur.service';

@Component({
  templateUrl: './conteneur-delete-dialog.component.html',
})
export class ConteneurDeleteDialogComponent {
  conteneur?: IConteneur;

  constructor(protected conteneurService: ConteneurService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.conteneurService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
