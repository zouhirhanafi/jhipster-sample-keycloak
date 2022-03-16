import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ParameterComponent } from '../list/parameter.component';
import { ParameterDetailComponent } from '../detail/parameter-detail.component';
import { ParameterUpdateComponent } from '../update/parameter-update.component';
import { ParameterRoutingResolveService } from './parameter-routing-resolve.service';

const parameterRoute: Routes = [
  {
    path: '',
    component: ParameterComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ParameterDetailComponent,
    resolve: {
      parameter: ParameterRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ParameterUpdateComponent,
    resolve: {
      parameter: ParameterRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ParameterUpdateComponent,
    resolve: {
      parameter: ParameterRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(parameterRoute)],
  exports: [RouterModule],
})
export class ParameterRoutingModule {}
