import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ConteneurComponent } from '../list/conteneur.component';
import { ConteneurDetailComponent } from '../detail/conteneur-detail.component';
import { ConteneurUpdateComponent } from '../update/conteneur-update.component';
import { ConteneurRoutingResolveService } from './conteneur-routing-resolve.service';

const conteneurRoute: Routes = [
  {
    path: '',
    component: ConteneurComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ConteneurDetailComponent,
    resolve: {
      conteneur: ConteneurRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ConteneurUpdateComponent,
    resolve: {
      conteneur: ConteneurRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ConteneurUpdateComponent,
    resolve: {
      conteneur: ConteneurRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(conteneurRoute)],
  exports: [RouterModule],
})
export class ConteneurRoutingModule {}
