import { TestBed, async, inject } from '@angular/core/testing';

import { DataEntryRouteGuardGuard } from './data-entry-route-guard.guard';

describe('DataEntryRouteGuardGuard', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [DataEntryRouteGuardGuard]
    });
  });

  it('should ...', inject([DataEntryRouteGuardGuard], (guard: DataEntryRouteGuardGuard) => {
    expect(guard).toBeTruthy();
  }));
});
