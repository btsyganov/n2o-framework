import React from 'react';
import { runSaga } from 'redux-saga';
import {
  closeModalEffect,
  refreshEffect,
  alertEffect,
  redirectEffect,
  messagesFormEffect,
  updateWidgetDependencyEffect,
} from './meta';
import { DATA_REQUEST } from '../constants/widgets';
import { ADD_MULTI, REMOVE_ALL } from '../constants/alerts';
import { ADD_FIELD_MESSAGE } from '../constants/formPlugin';
import { UPDATE_WIDGET_DEPENDENCY } from '../constants/dependency';

const setupCloseModal = () => {
  return closeModalEffect({
    payload: {
      widgetId: 'widget',
    },
    meta: {
      closeLastModal: true,
    },
  });
};

const setupRefresh = () => {
  const meta = {
    refresh: {
      type: 'widget',
      options: {
        widgetId: 'widgetId',
        options: {},
      },
    },
  };
  const refresh = refreshEffect({ meta });
  return {
    meta,
    refresh,
  };
};

const setupAlertEffect = () => {
  const meta = {
    alert: {
      alertKey: 'Page_Table',
      messages: [
        {
          label: 'Ошибка',
          text: 'Не удалось получить конфигурация приложения',
          closeButton: false,
          severity: 'danger',
        },
      ],
    },
  };
  const alert = alertEffect({
    meta,
  });
  return {
    meta,
    alert,
  };
};

const setupRedirectEffect = () => {
  const meta = {
    redirect: {
      path: '/n2o/data/1',
      pathMapping: {},
      queryMapping: {},
      target: 'application',
    },
  };
  const redirect = redirectEffect({
    meta,
  });
  return {
    meta,
    redirect,
  };
};

const setupMessageFormEffect = () => {
  const meta = {
    'messages.form': 'Page_Form',
    'messages.fields': [
      {
        name: 'field1',
        message: {
          severity: 'success',
          text: 'Успешное действие',
        },
      },
    ],
  };
  const messageForm = messagesFormEffect({
    meta,
  });
  return {
    meta,
    messageForm,
  };
};

const setupUpdateWidgetDependencyEffect = () => {
  const meta = {
    widgetId: 'testWidget',
  };

  return updateWidgetDependencyEffect({ meta });
};

describe('Сага для перехвата меты, сайд-эффектов из меты', () => {
  describe('Проверка саги alertEffect', () => {
    it('Проверяет диспатч экшена создания Alert', () => {
      const { alert } = setupAlertEffect();
      let gen = alert.next();
      expect(gen.value['PUT'].action.type).toEqual(REMOVE_ALL);
      gen = alert.next();
      expect(gen.value['PUT'].action.type).toEqual(ADD_MULTI);
    });

    it('Проверяет payload саги alertEffect', () => {
      const { alert, meta } = setupAlertEffect();
      let gen = alert.next();
      gen = alert.next();
      expect(gen.value['PUT'].action.payload.key).toEqual(meta.alert.alertKey);
      expect(gen.value['PUT'].action.payload.alerts[0].closeButton).toEqual(
        meta.alert.messages[0].closeButton
      );
      expect(gen.value['PUT'].action.payload.alerts[0].label).toEqual(
        meta.alert.messages[0].label
      );
      expect(gen.value['PUT'].action.payload.alerts[0].text).toEqual(
        meta.alert.messages[0].text
      );
      expect(gen.value['PUT'].action.payload.alerts[0].severity).toEqual(
        meta.alert.messages[0].severity
      );
    });
  });

  describe('Проверяет сагу refreshEffect', () => {
    it('Проверяет диспатч экшена обновления данных', () => {
      const { refresh } = setupRefresh();
      let { value } = refresh.next();
      expect(value['PUT'].action.type).toEqual(DATA_REQUEST);
    });

    it('Проверяет payload саги refreshEffect', () => {
      const { refresh, meta } = setupRefresh();
      let { value } = refresh.next();
      expect(value['PUT'].action.payload.widgetId).toEqual(
        meta.refresh.options.widgetId
      );
    });
  });

  describe('Проверяет сагу messagesFormEffect', () => {
    it('Проверка диспатча саги messagesFormEffect', () => {
      const { messageForm } = setupMessageFormEffect();
      let gen = messageForm.next();
      expect(gen.value['PUT'].action.payload[0].type).toEqual(
        ADD_FIELD_MESSAGE
      );
    });

    it('Проверка payload саги messageFormEffect', () => {
      const { messageForm, meta } = setupMessageFormEffect();
      let gen = messageForm.next();
      expect(gen.value['PUT'].action.payload[0].payload.form).toEqual(
        meta['messages.form']
      );
      expect(
        gen.value['PUT'].action.payload[0].payload.message.severity
      ).toEqual(meta['messages.fields'].severity);
      expect(gen.value['PUT'].action.payload[0].payload.message.text).toEqual(
        meta['messages.fields'].text
      );
    });

    describe('Проверяет сагу updateWidgetDependencyEffect', () => {
      it('Проверка диспатча саги', () => {
        const gen = setupUpdateWidgetDependencyEffect();
        expect(gen.next().value['PUT'].action.type).toEqual(
          UPDATE_WIDGET_DEPENDENCY
        );
        expect(gen.next().done).toEqual(true);
      });

      it('Проверка payload саги', () => {
        const gen = setupUpdateWidgetDependencyEffect();
        expect(gen.next().value['PUT'].action.payload).toEqual({
          widgetId: 'testWidget',
        });
        expect(gen.next().done).toEqual(true);
      });

      it('Проверка вызова всей цепочки', async () => {
        const dispatched = [];
        const fakeStore = {
          getState: () => ({}),
          dispatch: action => dispatched.push(action),
        };
        await runSaga(fakeStore, updateWidgetDependencyEffect, 'testWidget');
      });
    });
  });
});
