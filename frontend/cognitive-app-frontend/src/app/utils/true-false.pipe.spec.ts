import { TrueFalsePipe } from './true-false.pipe';

describe('TrueFalsePipe', () => {
  it('create an instance', () => {
    const pipe = new TrueFalsePipe();
    expect(pipe).toBeTruthy();
  });
});
