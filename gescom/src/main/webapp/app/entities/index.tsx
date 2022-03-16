import React from 'react';
import { Switch } from 'react-router-dom';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Article from './article';
import Categorie from './categorie';
import Commande from './commande';
import Facture from './facture';
/* jhipster-needle-add-route-import - JHipster will add routes here */

const Routes = ({ match }) => (
  <div>
    <Switch>
      {/* prettier-ignore */}
      <ErrorBoundaryRoute path={`${match.url}article`} component={Article} />
      <ErrorBoundaryRoute path={`${match.url}categorie`} component={Categorie} />
      <ErrorBoundaryRoute path={`${match.url}commande`} component={Commande} />
      <ErrorBoundaryRoute path={`${match.url}facture`} component={Facture} />
      {/* jhipster-needle-add-route-path - JHipster will add routes here */}
    </Switch>
  </div>
);

export default Routes;
