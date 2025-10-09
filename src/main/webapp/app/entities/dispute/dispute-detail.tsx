import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './dispute.reducer';

export const DisputeDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const disputeEntity = useAppSelector(state => state.dispute.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="disputeDetailsHeading">Dispute</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{disputeEntity.id}</dd>
          <dt>
            <span id="reason">Reason</span>
          </dt>
          <dd>{disputeEntity.reason}</dd>
          <dt>
            <span id="openedAt">Opened At</span>
          </dt>
          <dd>{disputeEntity.openedAt ? <TextFormat value={disputeEntity.openedAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="closedAt">Closed At</span>
          </dt>
          <dd>{disputeEntity.closedAt ? <TextFormat value={disputeEntity.closedAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>Order</dt>
          <dd>{disputeEntity.order ? disputeEntity.order.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/dispute" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/dispute/${disputeEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default DisputeDetail;
