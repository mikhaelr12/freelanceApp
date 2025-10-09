import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './delivery.reducer';

export const DeliveryDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const deliveryEntity = useAppSelector(state => state.delivery.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="deliveryDetailsHeading">Delivery</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{deliveryEntity.id}</dd>
          <dt>
            <span id="note">Note</span>
          </dt>
          <dd>{deliveryEntity.note}</dd>
          <dt>
            <span id="deliveredAt">Delivered At</span>
          </dt>
          <dd>
            {deliveryEntity.deliveredAt ? <TextFormat value={deliveryEntity.deliveredAt} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>Order</dt>
          <dd>{deliveryEntity.order ? deliveryEntity.order.id : ''}</dd>
          <dt>File</dt>
          <dd>{deliveryEntity.file ? deliveryEntity.file.objectKey : ''}</dd>
        </dl>
        <Button tag={Link} to="/delivery" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/delivery/${deliveryEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default DeliveryDetail;
