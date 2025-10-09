import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import OfferType from './offer-type';
import OfferTypeDetail from './offer-type-detail';
import OfferTypeUpdate from './offer-type-update';
import OfferTypeDeleteDialog from './offer-type-delete-dialog';

const OfferTypeRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<OfferType />} />
    <Route path="new" element={<OfferTypeUpdate />} />
    <Route path=":id">
      <Route index element={<OfferTypeDetail />} />
      <Route path="edit" element={<OfferTypeUpdate />} />
      <Route path="delete" element={<OfferTypeDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default OfferTypeRoutes;
