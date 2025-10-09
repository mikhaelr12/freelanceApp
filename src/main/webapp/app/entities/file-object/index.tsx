import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import FileObject from './file-object';
import FileObjectDetail from './file-object-detail';
import FileObjectUpdate from './file-object-update';
import FileObjectDeleteDialog from './file-object-delete-dialog';

const FileObjectRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<FileObject />} />
    <Route path="new" element={<FileObjectUpdate />} />
    <Route path=":id">
      <Route index element={<FileObjectDetail />} />
      <Route path="edit" element={<FileObjectUpdate />} />
      <Route path="delete" element={<FileObjectDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default FileObjectRoutes;
