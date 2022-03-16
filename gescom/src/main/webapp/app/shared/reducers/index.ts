import { loadingBarReducer as loadingBar } from 'react-redux-loading-bar';

import locale from './locale';
import authentication from './authentication';
import applicationProfile from './application-profile';

import administration from 'app/modules/administration/administration.reducer';
import userManagement from './user-management';
// prettier-ignore
import article from 'app/entities/article/article.reducer';
// prettier-ignore
import categorie from 'app/entities/categorie/categorie.reducer';
// prettier-ignore
import commande from 'app/entities/commande/commande.reducer';
// prettier-ignore
import facture from 'app/entities/facture/facture.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const rootReducer = {
  authentication,
  locale,
  applicationProfile,
  administration,
  userManagement,
  article,
  categorie,
  commande,
  facture,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
  loadingBar,
};

export default rootReducer;
