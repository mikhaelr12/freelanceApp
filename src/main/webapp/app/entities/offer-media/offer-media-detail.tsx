import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './offer-media.reducer';

export const OfferMediaDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const offerMediaEntity = useAppSelector(state => state.offerMedia.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="offerMediaDetailsHeading">Offer Media</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{offerMediaEntity.id}</dd>
          <dt>
            <span id="mediaKind">Media Kind</span>
          </dt>
          <dd>{offerMediaEntity.mediaKind}</dd>
          <dt>
            <span id="isPrimary">Is Primary</span>
          </dt>
          <dd>{offerMediaEntity.isPrimary ? 'true' : 'false'}</dd>
          <dt>
            <span id="caption">Caption</span>
          </dt>
          <dd>{offerMediaEntity.caption}</dd>
          <dt>
            <span id="createdDate">Created Date</span>
          </dt>
          <dd>
            {offerMediaEntity.createdDate ? <TextFormat value={offerMediaEntity.createdDate} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="lastModifiedDate">Last Modified Date</span>
          </dt>
          <dd>
            {offerMediaEntity.lastModifiedDate ? (
              <TextFormat value={offerMediaEntity.lastModifiedDate} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="createdBy">Created By</span>
          </dt>
          <dd>{offerMediaEntity.createdBy}</dd>
          <dt>
            <span id="lastModifiedBy">Last Modified By</span>
          </dt>
          <dd>{offerMediaEntity.lastModifiedBy}</dd>
          <dt>Offer</dt>
          <dd>{offerMediaEntity.offer ? offerMediaEntity.offer.name : ''}</dd>
          <dt>File</dt>
          <dd>{offerMediaEntity.file ? offerMediaEntity.file.objectKey : ''}</dd>
        </dl>
        <Button tag={Link} to="/offer-media" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/offer-media/${offerMediaEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default OfferMediaDetail;
