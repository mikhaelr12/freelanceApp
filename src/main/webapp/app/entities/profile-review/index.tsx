import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ProfileReview from './profile-review';
import ProfileReviewDetail from './profile-review-detail';
import ProfileReviewUpdate from './profile-review-update';
import ProfileReviewDeleteDialog from './profile-review-delete-dialog';

const ProfileReviewRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ProfileReview />} />
    <Route path="new" element={<ProfileReviewUpdate />} />
    <Route path=":id">
      <Route index element={<ProfileReviewDetail />} />
      <Route path="edit" element={<ProfileReviewUpdate />} />
      <Route path="delete" element={<ProfileReviewDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ProfileReviewRoutes;
