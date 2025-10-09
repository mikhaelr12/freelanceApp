import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getOrders } from 'app/entities/order/order.reducer';
import { getEntities as getFileObjects } from 'app/entities/file-object/file-object.reducer';
import { createEntity, getEntity, reset, updateEntity } from './delivery.reducer';

export const DeliveryUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const orders = useAppSelector(state => state.order.entities);
  const fileObjects = useAppSelector(state => state.fileObject.entities);
  const deliveryEntity = useAppSelector(state => state.delivery.entity);
  const loading = useAppSelector(state => state.delivery.loading);
  const updating = useAppSelector(state => state.delivery.updating);
  const updateSuccess = useAppSelector(state => state.delivery.updateSuccess);

  const handleClose = () => {
    navigate(`/delivery${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getOrders({}));
    dispatch(getFileObjects({}));
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
    values.deliveredAt = convertDateTimeToServer(values.deliveredAt);

    const entity = {
      ...deliveryEntity,
      ...values,
      order: orders.find(it => it.id.toString() === values.order?.toString()),
      file: fileObjects.find(it => it.id.toString() === values.file?.toString()),
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
          deliveredAt: displayDefaultDateTime(),
        }
      : {
          ...deliveryEntity,
          deliveredAt: convertDateTimeFromServer(deliveryEntity.deliveredAt),
          order: deliveryEntity?.order?.id,
          file: deliveryEntity?.file?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="freelanceApp.delivery.home.createOrEditLabel" data-cy="DeliveryCreateUpdateHeading">
            Create or edit a Delivery
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="delivery-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField
                label="Note"
                id="delivery-note"
                name="note"
                data-cy="note"
                type="text"
                validate={{
                  maxLength: { value: 1024, message: 'This field cannot be longer than 1024 characters.' },
                }}
              />
              <ValidatedField
                label="Delivered At"
                id="delivery-deliveredAt"
                name="deliveredAt"
                data-cy="deliveredAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <ValidatedField id="delivery-order" name="order" data-cy="order" label="Order" type="select">
                <option value="" key="0" />
                {orders
                  ? orders.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField id="delivery-file" name="file" data-cy="file" label="File" type="select">
                <option value="" key="0" />
                {fileObjects
                  ? fileObjects.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.objectKey}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/delivery" replace color="info">
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

export default DeliveryUpdate;
