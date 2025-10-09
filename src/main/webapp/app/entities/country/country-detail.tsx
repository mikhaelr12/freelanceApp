import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './country.reducer';

export const CountryDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const countryEntity = useAppSelector(state => state.country.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="countryDetailsHeading">Country</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{countryEntity.id}</dd>
          <dt>
            <span id="name">Name</span>
          </dt>
          <dd>{countryEntity.name}</dd>
          <dt>
            <span id="iso2">Iso 2</span>
          </dt>
          <dd>{countryEntity.iso2}</dd>
          <dt>
            <span id="iso3">Iso 3</span>
          </dt>
          <dd>{countryEntity.iso3}</dd>
          <dt>
            <span id="region">Region</span>
          </dt>
          <dd>{countryEntity.region}</dd>
          <dt>
            <span id="createdDate">Created Date</span>
          </dt>
          <dd>
            {countryEntity.createdDate ? <TextFormat value={countryEntity.createdDate} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="lastModifiedDate">Last Modified Date</span>
          </dt>
          <dd>
            {countryEntity.lastModifiedDate ? (
              <TextFormat value={countryEntity.lastModifiedDate} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="createdBy">Created By</span>
          </dt>
          <dd>{countryEntity.createdBy}</dd>
          <dt>
            <span id="lastModifiedBy">Last Modified By</span>
          </dt>
          <dd>{countryEntity.lastModifiedBy}</dd>
          <dt>
            <span id="active">Active</span>
          </dt>
          <dd>{countryEntity.active ? 'true' : 'false'}</dd>
        </dl>
        <Button tag={Link} to="/country" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/country/${countryEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default CountryDetail;
