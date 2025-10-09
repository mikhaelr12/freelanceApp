import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import OfferReview from './offer-review';
import OfferReviewDetail from './offer-review-detail';
import OfferReviewUpdate from './offer-review-update';
import OfferReviewDeleteDialog from './offer-review-delete-dialog';

const OfferReviewRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<OfferReview />} />
    <Route path="new" element={<OfferReviewUpdate />} />
    <Route path=":id">
      <Route index element={<OfferReviewDetail />} />
      <Route path="edit" element={<OfferReviewUpdate />} />
      <Route path="delete" element={<OfferReviewDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default OfferReviewRoutes;
