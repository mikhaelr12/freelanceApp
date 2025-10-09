import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import OfferMedia from './offer-media';
import OfferMediaDetail from './offer-media-detail';
import OfferMediaUpdate from './offer-media-update';
import OfferMediaDeleteDialog from './offer-media-delete-dialog';

const OfferMediaRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<OfferMedia />} />
    <Route path="new" element={<OfferMediaUpdate />} />
    <Route path=":id">
      <Route index element={<OfferMediaDetail />} />
      <Route path="edit" element={<OfferMediaUpdate />} />
      <Route path="delete" element={<OfferMediaDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default OfferMediaRoutes;
