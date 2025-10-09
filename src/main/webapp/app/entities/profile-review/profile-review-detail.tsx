import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './profile-review.reducer';

export const ProfileReviewDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const profileReviewEntity = useAppSelector(state => state.profileReview.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="profileReviewDetailsHeading">Profile Review</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{profileReviewEntity.id}</dd>
          <dt>
            <span id="text">Text</span>
          </dt>
          <dd>{profileReviewEntity.text}</dd>
          <dt>
            <span id="rating">Rating</span>
          </dt>
          <dd>{profileReviewEntity.rating}</dd>
          <dt>
            <span id="createdDate">Created Date</span>
          </dt>
          <dd>
            {profileReviewEntity.createdDate ? (
              <TextFormat value={profileReviewEntity.createdDate} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="lastModifiedDate">Last Modified Date</span>
          </dt>
          <dd>
            {profileReviewEntity.lastModifiedDate ? (
              <TextFormat value={profileReviewEntity.lastModifiedDate} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="createdBy">Created By</span>
          </dt>
          <dd>{profileReviewEntity.createdBy}</dd>
          <dt>
            <span id="lastModifiedBy">Last Modified By</span>
          </dt>
          <dd>{profileReviewEntity.lastModifiedBy}</dd>
          <dt>Reviewer</dt>
          <dd>{profileReviewEntity.reviewer ? profileReviewEntity.reviewer.id : ''}</dd>
          <dt>Reviewee</dt>
          <dd>{profileReviewEntity.reviewee ? profileReviewEntity.reviewee.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/profile-review" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/profile-review/${profileReviewEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default ProfileReviewDetail;
