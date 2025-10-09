import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Offer from './offer';
import OfferDetail from './offer-detail';
import OfferUpdate from './offer-update';
import OfferDeleteDialog from './offer-delete-dialog';

const OfferRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Offer />} />
    <Route path="new" element={<OfferUpdate />} />
    <Route path=":id">
      <Route index element={<OfferDetail />} />
      <Route path="edit" element={<OfferUpdate />} />
      <Route path="delete" element={<OfferDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default OfferRoutes;
