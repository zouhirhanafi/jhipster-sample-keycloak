import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import ParameterResolve from './route/parameter-routing-resolve.service';

const parameterRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/parameter.component').then(m => m.ParameterComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/parameter-detail.component').then(m => m.ParameterDetailComponent),
    resolve: {
      parameter: ParameterResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/parameter-update.component').then(m => m.ParameterUpdateComponent),
    resolve: {
      parameter: ParameterResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/parameter-update.component').then(m => m.ParameterUpdateComponent),
    resolve: {
      parameter: ParameterResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default parameterRoute;
