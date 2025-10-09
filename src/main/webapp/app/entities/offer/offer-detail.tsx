import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './offer.reducer';

export const OfferDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const offerEntity = useAppSelector(state => state.offer.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="offerDetailsHeading">Offer</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{offerEntity.id}</dd>
          <dt>
            <span id="name">Name</span>
          </dt>
          <dd>{offerEntity.name}</dd>
          <dt>
            <span id="description">Description</span>
          </dt>
          <dd>{offerEntity.description}</dd>
          <dt>
            <span id="rating">Rating</span>
          </dt>
          <dd>{offerEntity.rating}</dd>
          <dt>
            <span id="status">Status</span>
          </dt>
          <dd>{offerEntity.status}</dd>
          <dt>
            <span id="visibility">Visibility</span>
          </dt>
          <dd>{offerEntity.visibility ? 'true' : 'false'}</dd>
          <dt>
            <span id="createdDate">Created Date</span>
          </dt>
          <dd>{offerEntity.createdDate ? <TextFormat value={offerEntity.createdDate} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="lastModifiedDate">Last Modified Date</span>
          </dt>
          <dd>
            {offerEntity.lastModifiedDate ? <TextFormat value={offerEntity.lastModifiedDate} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="createdBy">Created By</span>
          </dt>
          <dd>{offerEntity.createdBy}</dd>
          <dt>
            <span id="lastModifiedBy">Last Modified By</span>
          </dt>
          <dd>{offerEntity.lastModifiedBy}</dd>
          <dt>Owner</dt>
          <dd>{offerEntity.owner ? offerEntity.owner.id : ''}</dd>
          <dt>Offertype</dt>
          <dd>{offerEntity.offertype ? offerEntity.offertype.name : ''}</dd>
          <dt>Tag</dt>
          <dd>
            {offerEntity.tags
              ? offerEntity.tags.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.name}</a>
                    {offerEntity.tags && i === offerEntity.tags.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
        </dl>
        <Button tag={Link} to="/offer" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/offer/${offerEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default OfferDetail;
