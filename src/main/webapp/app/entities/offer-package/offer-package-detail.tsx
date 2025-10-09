import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './offer-package.reducer';

export const OfferPackageDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const offerPackageEntity = useAppSelector(state => state.offerPackage.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="offerPackageDetailsHeading">Offer Package</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{offerPackageEntity.id}</dd>
          <dt>
            <span id="name">Name</span>
          </dt>
          <dd>{offerPackageEntity.name}</dd>
          <dt>
            <span id="description">Description</span>
          </dt>
          <dd>{offerPackageEntity.description}</dd>
          <dt>
            <span id="price">Price</span>
          </dt>
          <dd>{offerPackageEntity.price}</dd>
          <dt>
            <span id="currency">Currency</span>
          </dt>
          <dd>{offerPackageEntity.currency}</dd>
          <dt>
            <span id="deliveryDays">Delivery Days</span>
          </dt>
          <dd>{offerPackageEntity.deliveryDays}</dd>
          <dt>
            <span id="packageTier">Package Tier</span>
          </dt>
          <dd>{offerPackageEntity.packageTier}</dd>
          <dt>
            <span id="active">Active</span>
          </dt>
          <dd>{offerPackageEntity.active ? 'true' : 'false'}</dd>
          <dt>
            <span id="createdDate">Created Date</span>
          </dt>
          <dd>
            {offerPackageEntity.createdDate ? (
              <TextFormat value={offerPackageEntity.createdDate} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="lastModifiedDate">Last Modified Date</span>
          </dt>
          <dd>
            {offerPackageEntity.lastModifiedDate ? (
              <TextFormat value={offerPackageEntity.lastModifiedDate} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="createdBy">Created By</span>
          </dt>
          <dd>{offerPackageEntity.createdBy}</dd>
          <dt>
            <span id="lastModifiedBy">Last Modified By</span>
          </dt>
          <dd>{offerPackageEntity.lastModifiedBy}</dd>
          <dt>Offer</dt>
          <dd>{offerPackageEntity.offer ? offerPackageEntity.offer.name : ''}</dd>
        </dl>
        <Button tag={Link} to="/offer-package" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/offer-package/${offerPackageEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default OfferPackageDetail;
