import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntities } from './facture.reducer';
import { IFacture } from 'app/shared/model/facture.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const Facture = (props: RouteComponentProps<{ url: string }>) => {
  const dispatch = useAppDispatch();

  const factureList = useAppSelector(state => state.facture.entities);
  const loading = useAppSelector(state => state.facture.loading);

  useEffect(() => {
    dispatch(getEntities({}));
  }, []);

  const handleSyncList = () => {
    dispatch(getEntities({}));
  };

  const { match } = props;

  return (
    <div>
      <h2 id="facture-heading" data-cy="FactureHeading">
        <Translate contentKey="gescomApp.facture.home.title">Factures</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="gescomApp.facture.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to={`${match.url}/new`} className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="gescomApp.facture.home.createLabel">Create new Facture</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {factureList && factureList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>
                  <Translate contentKey="gescomApp.facture.id">ID</Translate>
                </th>
                <th>
                  <Translate contentKey="gescomApp.facture.client">Client</Translate>
                </th>
                <th>
                  <Translate contentKey="gescomApp.facture.date">Date</Translate>
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {factureList.map((facture, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`${match.url}/${facture.id}`} color="link" size="sm">
                      {facture.id}
                    </Button>
                  </td>
                  <td>{facture.client}</td>
                  <td>{facture.date ? <TextFormat type="date" value={facture.date} format={APP_LOCAL_DATE_FORMAT} /> : null}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`${match.url}/${facture.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${facture.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${facture.id}/delete`} color="danger" size="sm" data-cy="entityDeleteButton">
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="gescomApp.facture.home.notFound">No Factures found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default Facture;