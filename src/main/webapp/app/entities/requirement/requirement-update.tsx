import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getOrders } from 'app/entities/order/order.reducer';
import { createEntity, getEntity, reset, updateEntity } from './requirement.reducer';

export const RequirementUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const orders = useAppSelector(state => state.order.entities);
  const requirementEntity = useAppSelector(state => state.requirement.entity);
  const loading = useAppSelector(state => state.requirement.loading);
  const updating = useAppSelector(state => state.requirement.updating);
  const updateSuccess = useAppSelector(state => state.requirement.updateSuccess);

  const handleClose = () => {
    navigate(`/requirement${location.search}`);
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

    const entity = {
      ...requirementEntity,
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
      ? {}
      : {
          ...requirementEntity,
          order: requirementEntity?.order?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="freelanceApp.requirement.home.createOrEditLabel" data-cy="RequirementCreateUpdateHeading">
            Create or edit a Requirement
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="requirement-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField
                label="Prompt"
                id="requirement-prompt"
                name="prompt"
                data-cy="prompt"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                  maxLength: { value: 512, message: 'This field cannot be longer than 512 characters.' },
                }}
              />
              <ValidatedField
                label="Answer"
                id="requirement-answer"
                name="answer"
                data-cy="answer"
                type="text"
                validate={{
                  maxLength: { value: 2048, message: 'This field cannot be longer than 2048 characters.' },
                }}
              />
              <ValidatedField id="requirement-order" name="order" data-cy="order" label="Order" type="select">
                <option value="" key="0" />
                {orders
                  ? orders.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/requirement" replace color="info">
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

export default RequirementUpdate;
