import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getOrders } from 'app/entities/order/order.reducer';
import { createEntity, getEntity, reset, updateEntity } from './dispute.reducer';

export const DisputeUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const orders = useAppSelector(state => state.order.entities);
  const disputeEntity = useAppSelector(state => state.dispute.entity);
  const loading = useAppSelector(state => state.dispute.loading);
  const updating = useAppSelector(state => state.dispute.updating);
  const updateSuccess = useAppSelector(state => state.dispute.updateSuccess);

  const handleClose = () => {
    navigate(`/dispute${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getOrders({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    values.openedAt = convertDateTimeToServer(values.openedAt);
    values.closedAt = convertDateTimeToServer(values.closedAt);

    const entity = {
      ...disputeEntity,
      ...values,
      order: orders.find(it => it.id.toString() === values.order?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          openedAt: displayDefaultDateTime(),
          closedAt: displayDefaultDateTime(),
        }
      : {
          ...disputeEntity,
          openedAt: convertDateTimeFromServer(disputeEntity.openedAt),
          closedAt: convertDateTimeFromServer(disputeEntity.closedAt),
          order: disputeEntity?.order?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="freelanceApp.dispute.home.createOrEditLabel" data-cy="DisputeCreateUpdateHeading">
            Create or edit a Dispute
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="dispute-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField
                label="Reason"
                id="dispute-reason"
                name="reason"
                data-cy="reason"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                  maxLength: { value: 512, message: 'This field cannot be longer than 512 characters.' },
                }}
              />
              <ValidatedField
                label="Opened At"
                id="dispute-openedAt"
                name="openedAt"
                data-cy="openedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <ValidatedField
                label="Closed At"
                id="dispute-closedAt"
                name="closedAt"
                data-cy="closedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField id="dispute-order" name="order" data-cy="order" label="Order" type="select">
                <option value="" key="0" />
                {orders
                  ? orders.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/dispute" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">Back</span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp; Save
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default DisputeUpdate;
