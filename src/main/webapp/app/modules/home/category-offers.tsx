import './category-offers.scss';

import React, { useEffect, useMemo, useState } from 'react';
import axios from 'axios';
import { Link as RouterLink, useParams } from 'react-router-dom';

interface ICategoryDTO {
  id?: number;
  name?: string;
}

interface ISubcategoryDTO {
  id?: number;
  subcategoryId?: number;
  name?: string;
  category?: { id?: number; name?: string } | null;
  categoryShortDTO?: { categoryId?: number; name?: string } | null;
}

interface IOfferTypeShortDTO {
  id?: number;
  name?: string;
  subcategoryId?: number;
}

interface ISubcategoryOfferTypeGroup {
  subcategoryId: number;
  subcategoryName: string;
  offerTypes: Array<{ id: number; name: string }>;
}

const SUBCATEGORIES_PAGE_SIZE = 5000;

const CategoryOffers = () => {
  const { categoryId, subcategoryId } = useParams<'categoryId' | 'subcategoryId'>();
  const [categoryName, setCategoryName] = useState('Services');
  const [groups, setGroups] = useState<ISubcategoryOfferTypeGroup[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!categoryId && !subcategoryId) return;

    const loadSubcategoriesAndOfferTypes = async () => {
      setLoading(true);
      setError(null);

      try {
        let targetSubcategories: Array<{ id: number; name: string }> = [];
        let resolvedCategoryName = 'Services';
        let offerTypes: IOfferTypeShortDTO[] = [];

        if (subcategoryId) {
          const selectedId = Number(subcategoryId);
          if (!Number.isFinite(selectedId)) {
            setGroups([]);
            setError('Invalid subcategory id.');
            return;
          }

          const selectedSubcategoryResponse = await axios.get<ISubcategoryDTO[]>(
            `api/subcategories?page=0&size=1&id.equals=${subcategoryId}`,
          );
          const selectedSubcategory = selectedSubcategoryResponse.data?.[0];
          if (!selectedSubcategory) {
            setGroups([]);
            setError('Subcategory not found.');
            return;
          }
          const resolvedSubcategoryId = selectedSubcategory.id ?? selectedSubcategory.subcategoryId;
          const resolvedSubcategoryName = selectedSubcategory.name ?? 'Subcategory';

          if (!resolvedSubcategoryId) {
            setGroups([]);
            return;
          }

          resolvedCategoryName = selectedSubcategory.category?.name ?? selectedSubcategory.categoryShortDTO?.name ?? 'Services';
          const resolvedCategoryId = selectedSubcategory.category?.id ?? selectedSubcategory.categoryShortDTO?.categoryId;
          if (resolvedCategoryName === 'Services' && resolvedCategoryId) {
            try {
              const categoryResponse = await axios.get<ICategoryDTO>(`api/categories/${resolvedCategoryId}`);
              resolvedCategoryName = categoryResponse.data?.name ?? resolvedCategoryName;
            } catch {
              // Keep fallback category name.
            }
          }

          const offerTypesResponse = await axios.get<IOfferTypeShortDTO[]>(`api/offer-types/${selectedId}`);
          offerTypes = offerTypesResponse.data ?? [];
          targetSubcategories = [{ id: resolvedSubcategoryId, name: resolvedSubcategoryName }];
        } else {
          const parsedCategoryId = Number(categoryId);
          if (!Number.isFinite(parsedCategoryId)) {
            setGroups([]);
            setError('Invalid category id.');
            return;
          }

          const [categoryResponse, subcategoriesResponse, offerTypesResponse] = await Promise.all([
            axios.get<ICategoryDTO>(`api/categories/${categoryId}`),
            axios.get<ISubcategoryDTO[]>(
              `api/subcategories?page=0&size=${SUBCATEGORIES_PAGE_SIZE}&sort=name,asc&categoryId.equals=${categoryId}`,
            ),
            axios.get<IOfferTypeShortDTO[]>(`api/offer-types/category/${categoryId}`),
          ]);

          resolvedCategoryName = categoryResponse.data?.name ?? 'Category';
          offerTypes = offerTypesResponse.data ?? [];

          targetSubcategories = (subcategoriesResponse.data ?? [])
            .map(subcategory => ({
              id: subcategory.id ?? subcategory.subcategoryId,
              name: subcategory.name ?? 'Subcategory',
            }))
            .filter((subcategory): subcategory is { id: number; name: string } => typeof subcategory.id === 'number');
        }

        setCategoryName(resolvedCategoryName);

        if (targetSubcategories.length === 0) {
          setGroups([]);
          return;
        }

        const offerTypesBySubcategory = new Map<number, Array<{ id: number; name: string }>>();
        offerTypes.forEach(offerType => {
          if (typeof offerType.id === 'number' && typeof offerType.subcategoryId === 'number' && offerType.name) {
            const current = offerTypesBySubcategory.get(offerType.subcategoryId) ?? [];
            current.push({ id: offerType.id, name: offerType.name });
            offerTypesBySubcategory.set(offerType.subcategoryId, current);
          }
        });

        setGroups(
          targetSubcategories.map(subcategory => ({
            subcategoryId: subcategory.id,
            subcategoryName: subcategory.name,
            offerTypes: offerTypesBySubcategory.get(subcategory.id) ?? [],
          })),
        );
      } catch {
        setGroups([]);
        setError('Could not load category services right now.');
      } finally {
        setLoading(false);
      }
    };

    loadSubcategoriesAndOfferTypes();
  }, [categoryId, subcategoryId]);

  const normalizedGroups = useMemo(
    () =>
      groups.map(group => ({
        ...group,
        offerTypes: group.offerTypes.filter(offerType => offerType.name),
      })),
    [groups],
  );

  const totalOfferTypes = useMemo(() => normalizedGroups.reduce((sum, group) => sum + group.offerTypes.length, 0), [normalizedGroups]);

  return (
    <section className="category-offers-page">
      <div className="category-offers-header">
        <h1>{categoryName}</h1>
        <p>Browse all subcategories and their offer types.</p>
        <RouterLink to="/" className="back-home-link">
          Back to home
        </RouterLink>
      </div>

      {loading ? <div className="category-offers-state">Loading services...</div> : null}
      {!loading && error ? <div className="category-offers-state error">{error}</div> : null}
      {!loading && !error && normalizedGroups.length === 0 ? <div className="category-offers-state">No subcategories found.</div> : null}
      {!loading && !error && normalizedGroups.length > 0 && totalOfferTypes === 0 ? (
        <div className="category-offers-state">No offer types found for this category.</div>
      ) : null}

      {!loading && !error && normalizedGroups.length > 0 ? (
        <div className="subcategory-catalog-grid">
          {normalizedGroups.map((group, index) => (
            <section key={group.subcategoryId} className="subcategory-catalog-card">
              <RouterLink to={`/services/subcategory/${group.subcategoryId}`} className={`subcategory-visual palette-${index % 6}`}>
                <span>{group.subcategoryName}</span>
              </RouterLink>
              <header className="subcategory-catalog-header">
                <h2>{group.subcategoryName}</h2>
                <span>{group.offerTypes.length} offer types</span>
              </header>

              {group.offerTypes.length === 0 ? (
                <div className="category-offers-state compact">No offer types yet.</div>
              ) : (
                <ul className="offer-type-list">
                  {group.offerTypes.map(offerType => (
                    <li key={offerType.id}>
                      <RouterLink
                        className="offer-type-link"
                        to={`/services/offer-type/${offerType.id}?name=${encodeURIComponent(offerType.name)}`}
                      >
                        {offerType.name}
                      </RouterLink>
                    </li>
                  ))}
                </ul>
              )}
            </section>
          ))}
        </div>
      ) : null}
    </section>
  );
};

export default CategoryOffers;
