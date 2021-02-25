import { TestBed } from '@angular/core/testing';

import { QuickStatsServiceService } from './quick-stats-service.service';

describe('QuickStatsServiceService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: QuickStatsServiceService = TestBed.get(QuickStatsServiceService);
    expect(service).toBeTruthy();
  });
});
