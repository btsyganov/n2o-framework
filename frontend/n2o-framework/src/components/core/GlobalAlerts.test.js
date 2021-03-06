import React from 'react';
import { mount, shallow } from 'enzyme';
import sinon from 'sinon';
import { GlobalAlerts } from './GlobalAlerts';

const setup = (propsOverride = {}) => {
  const props = {
    alerts: [
      {
        id: 'test1',
        text: 'first text',
      },
      {
        id: 'test2',
        text: 'second text',
      },
    ],
  };

  return mount(<GlobalAlerts {...props} {...propsOverride} />);
};

describe('<GlobalAlerts />', () => {
  it('компонент должен отрисоваться', () => {
    const wrapper = setup();

    expect(wrapper.find('.n2o-global-alerts').exists()).toBeTruthy();
  });

  it('должны отрисоваться два алерта', () => {
    const wrapper = setup();

    expect(wrapper.find('Alert').length).toBe(2);
    expect(
      wrapper
        .find('.n2o-alert-body-text')
        .first()
        .text()
    ).toBe('first text');
    expect(
      wrapper
        .find('.n2o-alert-body-text')
        .last()
        .text()
    ).toBe('second text');
  });

  it('должен вызваться onDismiss', () => {
    const onDismiss = sinon.spy();
    const wrapper = setup({
      onDismiss,
    });

    wrapper
      .find('.n2o-alert-close')
      .first()
      .simulate('click');

    expect(onDismiss.calledOnce).toBeTruthy();
    expect(onDismiss.getCall(0).args[0]).toBe('test1');
  });
});
