import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Navigate, useLocation } from 'react-router-dom';

import { useAppSelector } from 'app/config/store';

interface IProfileRequiredRouteProps {
  children: React.ReactNode;
}

const ProfileRequiredRoute = ({ children }: IProfileRequiredRouteProps) => {
  const isAuthenticated = useAppSelector(state => state.authentication.isAuthenticated);
  const sessionHasBeenFetched = useAppSelector(state => state.authentication.sessionHasBeenFetched);
  const [isCheckingProfile, setIsCheckingProfile] = useState(false);
  const [hasProfile, setHasProfile] = useState<boolean | null>(null);
  const pageLocation = useLocation();

  useEffect(() => {
    let stillMounted = true;

    if (!sessionHasBeenFetched) {
      return () => {
        stillMounted = false;
      };
    }

    if (!isAuthenticated) {
      setIsCheckingProfile(false);
      setHasProfile(null);
      return () => {
        stillMounted = false;
      };
    }

    setIsCheckingProfile(true);
    axios
      .get('api/profiles/me')
      .then(() => {
        if (stillMounted) {
          setHasProfile(true);
        }
      })
      .catch(error => {
        if (!stillMounted) return;
        if (error?.response?.status === 404) {
          setHasProfile(false);
          return;
        }
        setHasProfile(false);
      })
      .finally(() => {
        if (stillMounted) {
          setIsCheckingProfile(false);
        }
      });

    return () => {
      stillMounted = false;
    };
  }, [isAuthenticated, sessionHasBeenFetched]);

  if (!sessionHasBeenFetched) {
    return <div></div>;
  }

  if (!isAuthenticated) {
    return <>{children}</>;
  }

  if (isCheckingProfile || hasProfile === null) {
    return <div></div>;
  }

  if (!hasProfile) {
    return <Navigate to="/profile/create" replace state={{ from: pageLocation }} />;
  }

  return <>{children}</>;
};

export default ProfileRequiredRoute;
