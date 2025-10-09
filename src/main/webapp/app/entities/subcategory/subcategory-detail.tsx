import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './subcategory.reducer';

export const SubcategoryDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const subcategoryEntity = useAppSelector(state => state.subcategory.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="subcategoryDetailsHeading">Subcategory</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{subcategoryEntity.id}</dd>
          <dt>
            <span id="name">Name</span>
          </dt>
          <dd>{subcategoryEntity.name}</dd>
          <dt>
            <span id="createdDate">Created Date</span>
          </dt>
          <dd>
            {subcategoryEntity.createdDate ? (
              <TextFormat value={subcategoryEntity.createdDate} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="lastModifiedDate">Last Modified Date</span>
          </dt>
          <dd>
            {subcategoryEntity.lastModifiedDate ? (
              <TextFormat value={subcategoryEntity.lastModifiedDate} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="createdBy">Created By</span>
          </dt>
          <dd>{subcategoryEntity.createdBy}</dd>
          <dt>
            <span id="lastModifiedBy">Last Modified By</span>
          </dt>
          <dd>{subcategoryEntity.lastModifiedBy}</dd>
          <dt>
            <span id="active">Active</span>
          </dt>
          <dd>{subcategoryEntity.active ? 'true' : 'false'}</dd>
          <dt>Category</dt>
          <dd>{subcategoryEntity.category ? subcategoryEntity.category.name : ''}</dd>
        </dl>
        <Button tag={Link} to="/subcategory" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/subcategory/${subcategoryEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default SubcategoryDetail;
