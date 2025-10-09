import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './offer-review.reducer';

export const OfferReviewDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const offerReviewEntity = useAppSelector(state => state.offerReview.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="offerReviewDetailsHeading">Offer Review</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{offerReviewEntity.id}</dd>
          <dt>
            <span id="text">Text</span>
          </dt>
          <dd>{offerReviewEntity.text}</dd>
          <dt>
            <span id="rating">Rating</span>
          </dt>
          <dd>{offerReviewEntity.rating}</dd>
          <dt>
            <span id="createdDate">Created Date</span>
          </dt>
          <dd>
            {offerReviewEntity.createdDate ? (
              <TextFormat value={offerReviewEntity.createdDate} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="lastModifiedDate">Last Modified Date</span>
          </dt>
          <dd>
            {offerReviewEntity.lastModifiedDate ? (
              <TextFormat value={offerReviewEntity.lastModifiedDate} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="createdBy">Created By</span>
          </dt>
          <dd>{offerReviewEntity.createdBy}</dd>
          <dt>
            <span id="lastModifiedBy">Last Modified By</span>
          </dt>
          <dd>{offerReviewEntity.lastModifiedBy}</dd>
          <dt>Offer</dt>
          <dd>{offerReviewEntity.offer ? offerReviewEntity.offer.name : ''}</dd>
          <dt>Reviewer</dt>
          <dd>{offerReviewEntity.reviewer ? offerReviewEntity.reviewer.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/offer-review" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/offer-review/${offerReviewEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default OfferReviewDetail;
