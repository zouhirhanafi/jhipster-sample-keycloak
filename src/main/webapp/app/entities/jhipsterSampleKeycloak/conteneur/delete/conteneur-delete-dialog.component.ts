import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IConteneur } from '../conteneur.model';
import { ConteneurService } from '../service/conteneur.service';

@Component({
  standalone: true,
  templateUrl: './conteneur-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class ConteneurDeleteDialogComponent {
  conteneur?: IConteneur;

  protected conteneurService = inject(ConteneurService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.conteneurService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
