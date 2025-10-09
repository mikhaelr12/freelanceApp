import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './file-object.reducer';

export const FileObjectDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const fileObjectEntity = useAppSelector(state => state.fileObject.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="fileObjectDetailsHeading">File Object</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{fileObjectEntity.id}</dd>
          <dt>
            <span id="bucket">Bucket</span>
          </dt>
          <dd>{fileObjectEntity.bucket}</dd>
          <dt>
            <span id="objectKey">Object Key</span>
          </dt>
          <dd>{fileObjectEntity.objectKey}</dd>
          <dt>
            <span id="contentType">Content Type</span>
          </dt>
          <dd>{fileObjectEntity.contentType}</dd>
          <dt>
            <span id="fileSize">File Size</span>
          </dt>
          <dd>{fileObjectEntity.fileSize}</dd>
          <dt>
            <span id="checksum">Checksum</span>
          </dt>
          <dd>{fileObjectEntity.checksum}</dd>
          <dt>
            <span id="durationSeconds">Duration Seconds</span>
          </dt>
          <dd>{fileObjectEntity.durationSeconds}</dd>
          <dt>
            <span id="createdDate">Created Date</span>
          </dt>
          <dd>
            {fileObjectEntity.createdDate ? <TextFormat value={fileObjectEntity.createdDate} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="lastModifiedDate">Last Modified Date</span>
          </dt>
          <dd>
            {fileObjectEntity.lastModifiedDate ? (
              <TextFormat value={fileObjectEntity.lastModifiedDate} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="createdBy">Created By</span>
          </dt>
          <dd>{fileObjectEntity.createdBy}</dd>
          <dt>
            <span id="lastModifiedBy">Last Modified By</span>
          </dt>
          <dd>{fileObjectEntity.lastModifiedBy}</dd>
        </dl>
        <Button tag={Link} to="/file-object" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/file-object/${fileObjectEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default FileObjectDetail;
