import findIndex from 'lodash/findIndex';
import isEmpty from 'lodash/isEmpty';
import isString from 'lodash/isString';
import get from 'lodash/get';
import reduce from 'lodash/reduce';
import has from 'lodash/has';

export const UNKNOWN_GROUP_FIELD_ID = '';

export const inArray = (array = [], item = {}) => {
  return array.some(arrayItem =>
    isString(item)
      ? arrayItem === item
      : arrayItem.id && item.id && arrayItem.id === item.id
  );
};

export const groupData = (data, groupFieldId) =>
  reduce(
    data,
    (acc, item) => {
      const key = get(item, groupFieldId) || UNKNOWN_GROUP_FIELD_ID;

      if (has(acc, key)) {
        acc[key].push(item);
      } else {
        acc[key] = [item];
      }

      return acc;
    },
    {}
  );

export const isDisabled = (item, selected, disabled) =>
  inArray(disabled, item) || inArray(selected, item);

const getNextNotDisabledId = (
  data,
  selected,
  disabled,
  initialId,
  distance,
  valueFieldId
) => {
  let index = findIndex(data, item => item[valueFieldId] === initialId);
  while (
    data[index + distance] &&
    isDisabled(data[index + distance], selected, disabled)
  ) {
    index += distance;
  }
  if (!data[index + distance]) return initialId;
  return data[index + distance][valueFieldId];
};

const getIdByDistance = (
  data,
  currentId,
  distance,
  valueFieldId,
  selected,
  disabled
) => {
  if (isEmpty(data)) return;
  const id = currentId ? currentId : data[0][valueFieldId];
  return getNextNotDisabledId(
    data,
    selected,
    disabled,
    id,
    distance,
    valueFieldId
  );
};

export const getFirstNotDisabledId = (
  data,
  selected,
  disabled,
  valueFieldId
) => {
  if (isEmpty(data)) return;
  if (!isDisabled(data[0], selected, disabled)) return data[0][valueFieldId];
  return getNextNotDisabledId(
    data,
    selected,
    disabled,
    data[0][valueFieldId],
    1,
    valueFieldId
  );
};

export const getNextId = (data, currentId, valueFieldId, selected, disabled) =>
  getIdByDistance(data, currentId, 1, valueFieldId, selected, disabled);

export const getPrevId = (data, currentId, valueFieldId, selected, disabled) =>
  getIdByDistance(data, currentId, -1, valueFieldId, selected, disabled);

export const isBottom = ({ scrollHeight, scrollTop, clientHeight }) =>
  Math.floor(scrollHeight - scrollTop) === clientHeight;
